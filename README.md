# Hazaro

Report hazards, spread awareness.

Android app for sharing nearby hazards (landslides, road closures, floods, and more) on a live map.

## Features

- Live map of community reports
- Browse reports without an account
- Sign in / create account to add a report
- Report type, description, and optional photo (camera or gallery)
- GPS location or tap the map to place a pin
- Zoom and “my location” controls
- Dark / light theme

## Setup

Add these to `local.properties` (do not commit this file):

```
MAPS_API_KEY=your_maps_key
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_UPLOAD_PRESET=your_upload_preset
```

Open the project in Android Studio and run on an emulator or device.

## TODOs

- [ ] Add photo later — attach photos to an existing report after submit
- [ ] Report history — list of reports the signed-in user has filed
- [ ] Voting — upvote / confirm a report so others can trust it
- [ ] Resolved — mark a hazard as cleared so it no longer looks active
- [ ] Profile level — rank or level based on reports and helpful votes
