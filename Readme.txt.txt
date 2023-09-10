1. Database 

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read access to all documents
    match /{document=**} {
      allow read: if true;
    }

    // Allow write access to authenticated users
    match /{document=**} {
      allow write: if request.auth != null;
    }
  }
}



2. Storage 
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      // Allow public read access, and authenticated write access
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}