package dev.pcrykh.pcrykh.storage;

import dev.pcrykh.pcrykh.model.AchievementDefinition;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class DataStore implements AutoCloseable {
    private final Connection connection;

    public DataStore(File dbFile) {
        try {
            dbFile.getParentFile().mkdirs();
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            initializeSchema();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to open SQLite database", e);
        }
    }

    private void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                    "player_uuid TEXT PRIMARY KEY," +
                    "spec_version TEXT NOT NULL," +
                    "achievement_completed_sum INTEGER NOT NULL," +
                    "player_level INTEGER NOT NULL," +
                    "ap_total INTEGER NOT NULL," +
                    "created_at INTEGER NOT NULL," +
                    "updated_at INTEGER NOT NULL" +
                    ")");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS achievement_progress (" +
                    "player_uuid TEXT NOT NULL," +
                    "achievement_id TEXT NOT NULL," +
                    "completed INTEGER NOT NULL," +
                    "progress_amount INTEGER NOT NULL," +
                    "updated_at INTEGER NOT NULL," +
                    "PRIMARY KEY (player_uuid, achievement_id)" +
                    ")");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS objective_history (" +
                    "player_uuid TEXT NOT NULL," +
                    "achievement_id TEXT NOT NULL," +
                    "completed_at INTEGER NOT NULL," +
                    "ap_awarded INTEGER NOT NULL" +
                    ")");
        }
    }

    public PlayerState loadPlayer(UUID playerId, String specVersion, List<AchievementDefinition> achievements) {
        PlayerState state = new PlayerState(playerId);
        long now = Instant.now().toEpochMilli();

        try {
            connection.setAutoCommit(false);
            if (!playerExists(playerId)) {
                insertPlayer(playerId, specVersion, now);
            }

            loadPlayerRow(playerId, state);
            loadAchievementProgress(playerId, achievements, state);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            throw new IllegalStateException("Failed to load player", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }

        return state;
    }

    private boolean playerExists(UUID playerId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM players WHERE player_uuid = ?")) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertPlayer(UUID playerId, String specVersion, long now) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO players (player_uuid, spec_version, achievement_completed_sum, player_level, ap_total, created_at, updated_at) VALUES (?, ?, 0, 0, 0, ?, ?)")) {
            ps.setString(1, playerId.toString());
            ps.setString(2, specVersion);
            ps.setLong(3, now);
            ps.setLong(4, now);
            ps.executeUpdate();
        }
    }

    private void loadPlayerRow(UUID playerId, PlayerState state) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT achievement_completed_sum, player_level, ap_total FROM players WHERE player_uuid = ?")) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    state.achievementTierSum = rs.getInt("achievement_completed_sum");
                    state.playerLevel = rs.getInt("player_level");
                    state.apTotal = rs.getInt("ap_total");
                }
            }
        }
    }

    private void loadAchievementProgress(UUID playerId, List<AchievementDefinition> achievements, PlayerState state) throws SQLException {
        Map<String, AchievementProgress> existing = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT achievement_id, completed, progress_amount FROM achievement_progress WHERE player_uuid = ?")) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AchievementProgress progress = new AchievementProgress();
                    progress.completed = rs.getInt("completed") == 1;
                    progress.progressAmount = rs.getLong("progress_amount");
                    existing.put(rs.getString("achievement_id"), progress);
                }
            }
        }

        for (AchievementDefinition def : achievements) {
            AchievementProgress progress = existing.get(def.id);
            if (progress == null) {
                progress = new AchievementProgress();
                progress.completed = false;
                progress.progressAmount = 0;
                upsertAchievementProgress(playerId, def.id, progress, Instant.now().toEpochMilli());
            }
            state.achievementProgress.put(def.id, progress);
        }
    }

    public void savePlayer(PlayerState state, String specVersion) {
        long now = Instant.now().toEpochMilli();
        try {
            connection.setAutoCommit(false);
                try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE players SET spec_version = ?, achievement_completed_sum = ?, player_level = ?, ap_total = ?, updated_at = ? WHERE player_uuid = ?")) {
                ps.setString(1, specVersion);
                ps.setInt(2, state.achievementTierSum);
                ps.setInt(3, state.playerLevel);
                ps.setInt(4, state.apTotal);
                ps.setLong(5, now);
                ps.setString(6, state.playerId.toString());
                ps.executeUpdate();
            }
            for (Map.Entry<String, AchievementProgress> entry : state.achievementProgress.entrySet()) {
                upsertAchievementProgress(state.playerId, entry.getKey(), entry.getValue(), now);
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            throw new IllegalStateException("Failed to save player", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    public void deletePlayerProgress(UUID playerId) {
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM achievement_progress WHERE player_uuid = ?")) {
                ps.setString(1, playerId.toString());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET achievement_completed_sum = 0, player_level = 0, ap_total = 0, updated_at = ? WHERE player_uuid = ?")) {
                ps.setLong(1, Instant.now().toEpochMilli());
                ps.setString(2, playerId.toString());
                ps.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            throw new IllegalStateException("Failed to reset player progress", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    private void upsertAchievementProgress(UUID playerId, String achievementId, AchievementProgress progress, long now) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO achievement_progress (player_uuid, achievement_id, completed, progress_amount, updated_at) VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT(player_uuid, achievement_id) DO UPDATE SET completed = excluded.completed, progress_amount = excluded.progress_amount, updated_at = excluded.updated_at")) {
            ps.setString(1, playerId.toString());
            ps.setString(2, achievementId);
            ps.setInt(3, progress.completed ? 1 : 0);
            ps.setLong(4, progress.progressAmount);
            ps.setLong(5, now);
            ps.executeUpdate();
        }
    }

    public void insertObjectiveHistory(UUID playerId, String achievementId, int apAwarded) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO objective_history (player_uuid, achievement_id, completed_at, ap_awarded) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, playerId.toString());
            ps.setString(2, achievementId);
            ps.setLong(3, Instant.now().toEpochMilli());
            ps.setInt(4, apAwarded);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert objective history", e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    public static class PlayerState {
        public final UUID playerId;
        public int achievementTierSum;
        public int playerLevel;
        public int apTotal;
        public final Map<String, AchievementProgress> achievementProgress = new HashMap<>();

        public PlayerState(UUID playerId) {
            this.playerId = playerId;
        }
    }

    public static class AchievementProgress {
        public boolean completed;
        public long progressAmount;
    }
}
