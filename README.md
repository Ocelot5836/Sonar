# Sonar

General 1.16 Mod utilties in one spot so they can be added to future mods without having to clone code over.

# How to add to your workspace

Insert the `plugins` block just below `buildscript`.

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version "4.0.4"
}
```

Add the shade configuration, repository, and the sonar dependency.

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
    compileOnly fg.deobf("com.github.Ocelot5836:Sonar:${project.sonar}:api")
    runtimeOnly fg.deobf("com.github.Ocelot5836:Sonar:${project.sonar}")
    shade "com.github.Ocelot5836.Sonar:${project.sonar}"
}
```

These remaining settings are added to allow the jar to build properly.

```gradle
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

Finally, choose the version of Sonar you wish to use and add the following to the `gradle.properties`

```properties
sonar=SonarVersion
```
