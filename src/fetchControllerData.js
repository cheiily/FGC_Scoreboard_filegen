function updateMetadata(metadata, flagsDir) {
    
    // Handle text content

    // Loop over each section
    for (let [section, sectiondata] of Object.entries(metadata)) {

        // Then the data within each section
        for (let [key, value] of Object.entries(sectiondata)) {
            try {
                document.querySelectorAll(`[metadata-value=${section}_${key}]`)
                .forEach(entry => entry.textContent = value);
            } catch(e) {};
        }
    }

    // Handle images
    document.querySelectorAll(`img[metadata-value=p1_nation]`)
    .forEach(entry => {
        if (metadata["p1"]["nation"] === "NULL" || metadata["p1"]["nation"] === "") {
            entry.src = flagsDir + "/null.png";
        }
        else {
            entry.src = flagsDir + "/" + metadata["p1"]["nation"] + ".png";
        }
    });
    
}