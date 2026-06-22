import os
import shutil

res_dir = 'TMessagesProj/src/main/res/'
# We want to replace default ic_launcher with one of the others that is presumably the penguin.
# Based on the file list, icon_2_launcher to icon_6_launcher exist.
# Let's check which one is the penguin or just replace ALL with a known one if we have it.
# The user said penguin icons are there but not applied.

mipmaps = [d for d in os.listdir(res_dir) if d.startswith('mipmap-')]
for m in mipmaps:
    path = os.path.join(res_dir, m)
    # If icon_2_launcher.png exists, let's assume it's the one we want (Teletux usually uses icon_2 or 3)
    # Actually, let's just make sure ic_launcher is replaced by something teletux.
    # I'll check for icon_2_launcher.png as a candidate.
    src = os.path.join(path, 'icon_2_launcher.png')
    dst = os.path.join(path, 'ic_launcher.png')
    if os.path.exists(src):
        shutil.copy2(src, dst)
        print(f"Copied {src} to {dst}")

    src_round = os.path.join(path, 'icon_2_launcher_round.png')
    dst_round = os.path.join(path, 'ic_launcher_round.png')
    if os.path.exists(src_round):
        shutil.copy2(src_round, dst_round)
        print(f"Copied {src_round} to {dst_round}")
