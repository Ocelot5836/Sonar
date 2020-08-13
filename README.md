# Sonar

General 1.15 Mod utilties in one spot so they can be added to future mods without having to clone code over.

# How to add to your workspace

Make sure to update your gradle version to 5.+ to use shadow.
Add the shadow plugin classpath to the buildscript block underneath the forge gradle classpath.

```gradle
classpath group: 'net.minecraftforge.gradle', name: 'ForgeGradle', version: '3.+', changing: true
classpath "com.github.jengelman.gradle.plugins:shadow:5.2.0"
```

Insert the shadow plugin underneath the forge gradle plugin.

```gradle
apply plugin: 'net.minecraftforge.gradle'
apply plugin: 'com.github.johnrengelman.shadow'
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
