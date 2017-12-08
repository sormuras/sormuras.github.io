# Install JDK on Travis CI and other places

## Master Copy 
https://github.com/sormuras/bach/blob/master/install-jdk.sh

```
#!/bin/bash
set -e

JDK_FEATURE=10

TMP=$(curl -L jdk.java.net/${JDK_FEATURE})
TMP="${TMP#*Most recent build: jdk-${JDK_FEATURE}-ea+}" # remove everything before the number
TMP="${TMP%%<*}"                                        # remove everything after the number
JDK_BUILD="$(echo -e "${TMP}" | tr -d '[:space:]')"     # remove all whitespace

JDK_ARCHIVE=jdk-${JDK_FEATURE}-ea+${JDK_BUILD}_linux-x64_bin.tar.gz

cd ~
wget http://download.java.net/java/jdk${JDK_FEATURE}/archive/${JDK_BUILD}/binaries/${JDK_ARCHIVE}
tar -xzf ${JDK_ARCHIVE}
export JAVA_HOME=~/jdk-${JDK_FEATURE}
export PATH=${JAVA_HOME}/bin:$PATH
cd -

java --version
```

## Usages

* junit5 https://github.com/junit-team/junit5/tree/master/src/install
* pro https://github.com/forax/pro/tree/master/.travis
* bytebuddy https://github.com/raphw/byte-buddy/tree/master/.travis
* google-java-format https://github.com/google/google-java-format/blob/master/scripts