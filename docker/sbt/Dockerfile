# Getting the latest version of ArchLinux
FROM archlinux:latest
RUN echo "y" | pacman -Syu

# Getting Scala SBT
RUN echo "y" | pacman -S jre-openjdk sbt

# Getting helptools
RUN echo "y" | pacman -S git neovim

# Adding this project to the image
COPY . /Saturn
WORKDIR Saturn

# Configuring sbt
RUN sbt compile
