function updateMetadata(metadata, flagsDir) {

    // Handle text content

    // Loop over each section
    for (let [section, sectiondata] of Object.entries(metadata)) {

        // Then the data within each section
        for (let [key, value] of Object.entries(sectiondata)) {
            try {
                document.querySelectorAll(`span[metadata-value=${section}_${key}]`)
                .forEach(entry => entry.textContent = value);
            } catch(e) {};
        }
    }

    // Handle images
    document.querySelectorAll(`img[metadata-value=p1_nation]`)
    .forEach(entry => {
        entry.src = metadata["p1"]["nation"]; 
    });

    document.querySelectorAll(`img[metadata-value=p2_nation]`)
    .forEach(entry => {
        entry.src = metadata["p2"]["nation"]; 
    });
    
}