# Sonar

General 1.15 Mod utilties in one spot so they can be added to future mods without having to clone code over.

# How to add to your workspace

Insert the `plugins` block just below `buildscript`.

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version "4.0.4"
}
```

Add the shade configuration, repository, and the sonar dependency. Note `runtimeOnly` and `compileOnly` are used to allow sources to attach properly.

```gradle
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
    compileOnly "com.github.Ocelot5836:Sonar:${project.sonar}"
    runtimeOnly fg.deobf("com.github.Ocelot5836:Sonar:${project.sonar}")
    shade fg.deobf("com.github.Ocelot5836:Sonar:${project.sonar}")
}
```

These remaining settings are added to allow the jar to build properly.

```gradle
shadowJar {
    configurations = [project.configurations.shade]
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

Finally, choose the version of Sonar you wish to use and add the following to the `gradle.properties`

```properties
sonar=SonarVersion
```

## Optional

If you want, you can add this so Sonar will be compiled into a different package to allow better compatibility.

```gradle
shadowJar {
    configurations = [project.configurations.shade]
    relocate 'io.github.ocelot', 'your.project.lib.ocelot'
}
```
