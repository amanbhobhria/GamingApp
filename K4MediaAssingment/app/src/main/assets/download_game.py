import subprocess
import os

# Game URL
game_url = 'https://g.h5games.online/stack-jump/'  # Replace with your URL

# Directory to store downloaded game files


download_dir = '/data/data/com.example.k4mediaassingment/files/h5games'
# download_dir = '/path/to/download/h5games'  # Adjust this to your desired path

# Download the game using wayback_machine_downloader
download_command = f"wayback_machine_downloader {game_url} -d {download_dir}"

# Run the download command
subprocess.run(download_command, shell=True)

print(f"Game downloaded from {game_url} to {download_dir}")
