import admin from "firebase-admin";
import serviceAccount from "./backstreetcycles-eaf92-firebase-adminsdk-cg8p4-29add28d51.json";
import { initializeApp } from 'firebase/app';
import { getFirestore, collection, addDoc, doc, setDoc, writeBatch } from 'firebase/firestore';
import fetch from 'node-fetch';


import Faker from 'faker';

// Replace the following with your app's Firebase project configuration
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
//const db = app.firestore();

const firebaseAdmin = admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});


async function seedUsers() {
    // THERE IS A LIMIT OF 500 users only add at a time.
    const numberOfUser = 10
    const userList = [];
    const batch = writeBatch(db);
    for (let i = 0; i < numberOfUser; i++) {
        const firstName = Faker.name.firstName();
        const lastName = Faker.name.lastName();
        const email = Faker.internet.email();
        const password = Faker.internet.password();

        const docRef = doc(collection(db, "users"));

        const uid = docRef.id;
        const user = { firstName, lastName, email, uid };
        userList.push({ email, password, uid });
        batch.set(docRef, user);
    }

    await batch.commit();

    await firebaseAdmin
        .auth().importUsers(userList);

}

async function seedDocks() {

    const res = await fetch("https://api.tfl.gov.uk/BikePoint/");
    const resJson = await res.json();
    await parseToObject(resJson)
}

async function parseToObject(response) {
    console.log(response.length)
    console.log("loading adding....")

    let currentBatchIndex = 0;
    const batchesList = [writeBatch(db)];

    for (let i = 0; i < response.length; i++) {

        const batchChecker = Math.floor(i % 500);

        if (batchChecker === 0) {
            const newBatch = writeBatch(db);
            batchesList.push(newBatch);
            ++currentBatchIndex;
        }

        const id = response[i].id;
        const name = response[i].commonName;
        const lat = response[i].lat;
        const lon = response[i].lon;
        const nbBikes = checkValidity(response[i].additionalProperties[6].value);
        const nbSpaces = checkValidity(response[i].additionalProperties[7].value);
        const nbDocks = checkValidity(response[i].additionalProperties[8].value);

        if (validDock(nbBikes, nbSpaces, nbDocks)) {
            const dock = { id, name, lat, lon, nbBikes, nbSpaces, nbDocks };
            try {
                const docRef = doc(db, "docStation", dock.id);
                batchesList[currentBatchIndex].set(docRef, dock, { merge: true });
            }
            catch (e) {
                console.log(e)
            }
        }
    }
    await Promise.all(batchesList.map((batch => batch.commit())));

}

function validDock(nbBikes, nbSpaces, nbDocks) {
    return (nbDocks - (nbBikes + nbSpaces) === 0);
}

function checkValidity(value) {
    try {
        return parseInt(value);
    }
    catch (e) {
        return 0;
    }
}



async function seed() {
    try {

        const res = await Promise.all([
            seedUsers(),
            seedDocks(),
        ])
        process.exit()
    } catch (err) {
        console.log('err', err)
    }
}

seed()