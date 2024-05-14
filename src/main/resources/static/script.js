function checkFiles(files) {
    console.log(files);

    if (files.length != 1) {
        alert("Bitte genau eine Datei hochladen.")
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("Datei zu gross (max. 10Mb)");
        return;
    }

    previewPart.style.visibility ="visible";
    preview.style.visibility ="visible";
    answerPart.style.visibility = "visible";
    const file = files[0];

    // Preview
    if (file) {
        preview.src = URL.createObjectURL(files[0])
    }

    // Upload
    const formData = new FormData();
    for (const name in files) {
        formData.append("image", files[name]);
    }

    fetch('/analyze', {
        method: 'POST',
        body: formData
    }).then(response => {
        // Überprüfen Sie den Status der Antwort
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        // Extrahieren Sie die JSON-Daten aus der Antwort
        return response.json();
    }).then(jsonData => {
        // Verarbeiten Sie die erhaltenen JSON-Daten
        console.log(jsonData);
        // Rufen Sie die Funktion formatAnswer auf, um die JSON-Daten zu formatieren
        const formattedAnswer = formatAnswer(jsonData);
        // Fügen Sie das formatierte Ergebnis in das HTML ein
        document.getElementById('answer').innerHTML = formattedAnswer;
    }).catch(error => {
        // Fangen Sie Fehler ab und geben Sie sie aus
        console.error('Error:', error);
    });
}


function formatAnswer(jsonData) {
    let formattedAnswer = '';
    const emotions = {
        'happy': 'Glücklich',
        'sad': 'Traurig',
        'angry': 'Verärgert',
        'fearful': 'Ängstlich',
        'surprised': 'Überrascht',
        'disgusted': 'Angeekelt',
        'neutral': 'Neutral'
    };

    // Bildpfade für Emotionen
    const emotionImages = {
        happy: "images/gluecklich.png",
        sad: "images/sad.png",
        angry: "images/angry.png",
        fearful: "images/fearful.png",
        surprised: "images/surprised.png",
        disgusted: "images/disgusted.png",
        neutral: "images/neutral.png"
    };

    jsonData.forEach(entry => {
        const className = emotions[entry.className];
        const probability = entry.probability.toFixed(4);
        const imageSrc = emotionImages[entry.className];
        formattedAnswer += `<div style="display: flex; align-items: center;"><img src="${imageSrc}" style="width: 5%; margin-right: 10px;"><p style="margin: 0;">${className}: ${probability}</p></div>`;
    });

    return formattedAnswer;

}

const formattedAnswer = formatAnswer(jsonData);
document.getElementById('answer').innerHTML = formattedAnswer;