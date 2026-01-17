# Build and export

- node: docs/runtime/build.md
  - purpose:
    - define canonical build outputs
    - define docker export behavior for `data/pcrykh.jar`
  - jar_output:
    - task: `exportJar`
    - output_path: `data/plugins/pcrykh.jar`
    - alias_task: `cleanExport` (runs `clean` then `exportJar`)
  - docker_export:
    - Dockerfile provides a single-purpose export image
    - dockerfile_path: `Dockerfile`
    - build output is a single file at `/data/pcrykh.jar`
    - build system MUST be Gradle 8.7 with JDK 21 toolchain
  - docker_usage:
    - build command MUST use BuildKit output to local filesystem
    - output destination MUST be `./data`
    - resulting file MUST be `./data/pcrykh.jar`
