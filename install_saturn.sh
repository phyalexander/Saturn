#!/bin/bash

# Создаем папку ".Saturn" в домашней папке пользователя
mkdir -p $HOME/.Saturn

# Определяем директорию, где находится сам скрипт
SCRIPT_DIR=$(dirname "\$0")

# Копируем файл "jars/Saturn.jar", папку "language/std" и исполняемый скрипт в директорию ".Saturn"
cp -r "$SCRIPT_DIR/jars/Saturn.jar" "$SCRIPT_DIR/language/std" $HOME/.Saturn/
cp -r "$SCRIPT_DIR/saturn.sh" ~/.Saturn/

# изменяем режим скрипта на исполняемый
chmod +x $HOME/.Saturn/saturn.sh

# Добавляем директорию ".Saturn" в PATH
echo "\n## Saturn language" >> ~/.bashrc
echo "export PATH=\$PATH:$HOME/.Saturn" >> ~/.bashrc

echo "\n## Saturn language" >> ~/.zshrc
echo "export PATH=\$PATH:$HOME/.Saturn" >> ~/.zshrc

# Удаляем git-репозиторий
cd ..
rm -rf Saturn
