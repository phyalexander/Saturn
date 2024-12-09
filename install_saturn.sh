#!/bin/bash

# Create directory ".Saturn" in the user's home directory
mkdir -p $HOME/.Saturn

# Determine where this script is located
SCRIPT_DIR=$(dirname "\$0")

# Copy file "jars/Saturn.jar", directory "language/std"
# and executable script into ".Saturn"
cp -r "$SCRIPT_DIR/jars/Saturn.jar" "$SCRIPT_DIR/language/std" $HOME/.Saturn/
cp -r "$SCRIPT_DIR/saturn.sh" $HOME/.Saturn/

# Change script mode to executable
chmod +x $HOME/.Saturn/saturn.sh

# Adding directory ".Saturn" to PATH
if grep -q "## Saturn language" $HOME/.bashrc; then
     echo "Saturn is already in PATH"
else
    echo "" >> ~/.bashrc
    echo "## Saturn language" >> $HOME/.bashrc
    echo "export PATH=\$PATH:$HOME/.Saturn" >> $HOME/.bashrc

    echo "" >> ~/.zshrc
    echo "## Saturn language" >> $HOME/.zshrc
    echo "export PATH=\$PATH:$HOME/.Saturn" >> $HOME/.zshrc
fi
