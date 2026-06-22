import os
import re

def replace_in_file(filepath, search, replace):
    with open(filepath, 'r') as f:
        content = f.read()
    new_content = re.sub(search, replace, content)
    if content != new_content:
        with open(filepath, 'w') as f:
            f.write(new_content)
        return True
    return False

# Replace in strings.xml (English)
replace_in_file('TMessagesProj/src/main/res/values/strings.xml', r'\bTelegram\b', 'TeleTux')

# More aggressive replacement might be needed for other locales if they exist
# Let's find all strings.xml files
for root, dirs, files in os.walk('TMessagesProj/src/main/res'):
    for file in files:
        if file == 'strings.xml':
            replace_in_file(os.path.join(root, file), r'\bTelegram\b', 'TeleTux')
