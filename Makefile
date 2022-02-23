seed:
	node commands/seed.js

unseed:
	node commands/unseed.js

emulator:
	/Users/magga/Library/Android/sdk/emulator/emulator -avd Pixel_4_API_30
	adb install /Users/magga/AndroidStudioProjects/Backstreet_cycles/app/build/intermediates/apk/debug/app-debug.apk

install:
	npm install xhr2
	npm install firebase
	npm install firebase-admin --save
	npm init
	npm install node-fetch@2