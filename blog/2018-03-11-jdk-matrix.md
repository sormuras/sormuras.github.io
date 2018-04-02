# JDK Matrix on Travis CI

Now sports JDK from version [8 up to 11](https://travis-ci.org/sormuras/sormuras.github.io).

[<img src="2018-03-11-jdk-matrix-screenshot.png">](https://travis-ci.org/sormuras/sormuras.github.io)

## Configuration

Download `install-jdk.sh` on-the-fly and let it install most recent JDK versions.

`.travis.yml`

```yml
sudo: false
dist: trusty
language: java

before_install:
  - wget https://github.com/sormuras/bach/raw/master/install-jdk.sh

matrix:
  include:
# 8
    - env: JDK='Oracle JDK 8'
      jdk: oraclejdk8
    - env: JDK='OpenJDK 8'
      jdk: openjdk8
# 9
    - env: JDK='Oracle JDK 9'
      jdk: oraclejdk9
    - env: JDK='OpenJDK 9'
      install: . ./install-jdk.sh -F 9
# 10
    - env: JDK='Oracle JDK 10'
      install: . ./install-jdk.sh -F 10 -L BCL
    - env: JDK='OpenJDK 10'
      install: . ./install-jdk.sh -F 10 -L GPL
# 11
    - env: JDK='Oracle JDK 11'
      install: . ./install-jdk.sh -F 11 -L BCL
    - env: JDK='OpenJDK 11'
      install: . ./install-jdk.sh -F 11 -L GPL

script:
  - echo PATH = ${PATH}
  - echo JAVA_HOME = ${JAVA_HOME}
  - java -version
```
