<div align="center">
  <a href="https://github.com/BlackOfWorld/SneakyBastard">
    <img src="assets/logo.png" alt="Logo" width="480" height="480">
  </a>

<h3 align="center">SneakyBastard</h3>

  <p align="center">
    An open-source malicious plugin for Spigot servers.
    <br />
    <a href="https://github.com/BlackOfWorld/SneakyBastard/issues">Report Bug</a>
    ·
    <a href="https://github.com/BlackOfWorld/SneakyBastard/issues">Request Feature</a>
  </p>
</div>

# Introduction
SneakyBastard is a continuation of an old "malicious"
Spigot plugin of mine called [BlackHat](https://github.com/BlackOfWorld/BlackHat).

However, since Spigot 1.17+, obfuscation mappings have been **removed**, resulting in BlackHat being incompatible starting from that version.

<font size=4>**This project is in heavy development and is not ready for production use.**</font>

# Tests

Testing is not planned as this project heavily relies on NMS (*net.minecraft.server*) code, so testing will be unfeasible for a small project like this.

# Run Locally

First, download and run [BuildTools](https://www.spigotmc.org/wiki/buildtools/) and execute like this:
```
java -jar BuildTools.jar --remapped --generate-source
```
After it's finished executing, open IntelliJ IDE and load pom.xml

Then just run "package" maven build and your plugin will be in the `source` folder :)

# License
Everything is under [GNU GPL v3](LICENSE) license unless stated otherwise.