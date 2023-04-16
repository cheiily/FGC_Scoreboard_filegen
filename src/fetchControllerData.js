async function updateMetadata() {
    let metadata;

    try {
        metadata = await (await fetch("http://127.0.0.1:52068/metadata", {"Content-Type": "application/json"})).json()
    } catch(e) {console.log(`Failed to connect to server. \n${e}`)}
    
    //Loop over each section
    for (let [section, sectiondata] of Object.entries(metadata)) {

        // Then the data within each section
        for (let [key, value] of Object.entries(sectiondata)) {
            try {
                document.getElementById(`${section}_${key}`).textContent = value;
            } catch(e) {};
        }
    }

}