# JDK on Mac OS X

`bach/install-jdk.sh` now supports installing latest-and-greated JDKs on Mac OS X.
Find the :apple: in the image below...

![2018-05-29-jdk-matrix-screenshot.png](2018-05-29-jdk-matrix-screenshot.png)


## Configuration

`.travis.yml`

```yml
os: osx
env: JDK='OpenJDK 11-ea'

before_install:
- unset -f cd
- wget https://github.com/sormuras/bach/raw/master/install-jdk.sh

install:
- . ./install-jdk.sh
```
