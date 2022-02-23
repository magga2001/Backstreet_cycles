import { initializeApp } from 'firebase/app';
import { getFirestore, collection, getDocs, addDoc, doc, setDoc } from 'firebase/firestore';
import pkg from 'xhr2';
const { XMLHttpRequest } = pkg;

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
//const db = app.firestore();

function seed()
{
    let xhr = new XMLHttpRequest();
    xhr.open('GET', "https://api.tfl.gov.uk/BikePoint/", true);
    xhr.send();

    xhr.onreadystatechange = function () {
    if (xhr.readyState === 4) {
        try {
            let response = JSON.parse(xhr.responseText);

            parseToObject(response);

        } catch (e) {
            console.log("ERROR 404")
        }
    }
};

}

async function parseToObject(response)
{
    console.log(response.length)
    console.log("loading adding....")
        for(let i = 0; i < response.length; i++)
        {
            const id = response[i].id;
            const name = response[i].commonName;
            const lat = response[i].lat;
            const lon = response[i].lon;
            const nbBikes = checkValidity(response[i].additionalProperties[6].value);
            const nbSpaces = checkValidity(response[i].additionalProperties[7].value);
            const nbDocks = checkValidity(response[i].additionalProperties[8].value);

            if(validDock(nbBikes, nbSpaces, nbDocks))
            {
                const dock = {id,name,lat,lon,nbBikes,nbSpaces,nbDocks};
                try
                {
                    const docRef = doc(db, "docStation", dock.id);
                    setDoc(docRef, dock,{merge:true})
                    //db.collection("docStation").doc(dock.id).set(dock,{merge:true})
                }
                catch(e)
                {
                    console.log(e)
                }
            }
        }
        //console.log("Added successfully...")
}

function validDock(nbBikes, nbSpaces, nbDocks)
{
    return (nbDocks - (nbBikes + nbSpaces) === 0);
}

function checkValidity(value)
{
    try {
      return parseInt(value);
    }
    catch (e) {
      return 0;
    }
}

seed()

