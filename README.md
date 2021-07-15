[![Release](https://jitpack.io/v/Ocelot5836/Sonar.svg)](https://jitpack.io/#Ocelot5836/Sonar)

# Sonar

General 1.16 Mod utilties in one spot so they can be added to future mods without having to clone code over.

# How to add to your workspace

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version "4.0.4"
}

configurations {
    shade
}

repositories {
    maven {
        name = "JitPack"
        url = "https://jitpack.io"
    }
}

dependencies {
    implementation fg.deobf("com.github.Ocelot5836:Sonar:version")
    shade "com.github.Ocelot5836.Sonar:${project.sonar}"
}

shadowJar {
    configurations = [project.configurations.shade]
    relocate 'io.github.ocelot', 'your.project.lib.ocelot'
}

reobf {
    shadowJar {}
}

artifacts {
    archives jar
    archives shadowJar
}

build.dependsOn reobfShadowJar
```
