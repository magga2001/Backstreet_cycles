import { initializeApp } from 'firebase/app';
import { getFirestore, collection, getDocs, doc, query, orderBy, limit , writeBatch} from 'firebase/firestore';


// Follow this pattern to import other Firebase services
// import { } from 'firebase/<service>';

// TODO: Replace the following with your app's Firebase project configuration
const firebaseConfig = {
    apiKey: "AIzaSyAR7RfpG0E0RF_Rk3NQxx36jGeV2rt5ITs",
    authDomain: "backstreetcycles-eaf92.firebaseapp.com",
    projectId: "backstreetcycles-eaf92",
    storageBucket: "backstreetcycles-eaf92.appspot.com",
    messagingSenderId: "118932775845",
    appId: "1:118932775845:web:465cc8b3151d019139be4c",
    measurementId: "G-L7BP9K8L8T"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

async function deleteCollection(db, collectionPath, batchSize) {
  const collectionRef = collection(db, collectionPath);
  const q = query(collectionRef, orderBy("name"), limit(batchSize));

  return new Promise((resolve, reject) => {
    deleteQueryBatch(db, q, resolve).catch(reject);
  });
}

async function deleteQueryBatch(db, q, resolve) {
  const snapshot = await getDocs(q);

  const batchSize = snapshot.size;
  if (batchSize === 0) {
    // When there are no documents left, we are done
    resolve();
    return;
  }

  // Delete documents in a batch
  const batch = writeBatch(db);
  snapshot.forEach((doc) => {
    batch.delete(doc.ref);
  });
  await batch.commit();

  // Recurse on the next process tick, to avoid
  // exploding the stack.
  process.nextTick(() => {
    deleteQueryBatch(db, q, resolve);
  });
}
function unseed(){
console.log("deleting...")
deleteCollection(db,"docStation",500)
}
unseed()