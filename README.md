# Sonar

General 1.15 Mod utilties in one spot so they can be added to future mods without having to clone code over.

# How to add to your workspace

## Note: Verify your gradle version is 5.+ to use shadow.
Insert the `plugins` block just below `buildscript`.

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version "5.2.0"
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
    runtimeOnly fg.deobf("com.github.Ocelot5836:Sonar:SonarVersion")
    compileOnly "com.github.Ocelot5836:Sonar:SonarVersion"
    shade "com.github.Ocelot5836:Sonar:SonarVersion"
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
