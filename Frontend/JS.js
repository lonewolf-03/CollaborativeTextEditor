window.addEventListener("load", function() {
    const docs = [
      {
        name: "Doc1",
        owner: "me",
        date: "Apr 4, 2024"
      },
      {
        name: "Doc2",
        owner: "me",
       date: "Apr 3, 2024"
      },
      {
        name: "Doc3",
        owner: "me",
        date: "Mar 6, 2024"
      },
      {
        name: "Doc2",
        owner: "me",
        date: "May 9, 2023"
      },
      // add more documents to the array
    ];
  
    const documentList = document.getElementById("document-list");
    const createDocumentBtn = document.getElementById("create-document-btn");
    const createDocumentModal = document.getElementById("create-document-modal");
    const documentNameInput = document.getElementById("document-name-input");
    const createDocumentSubmitBtn = document.getElementById("create-document-submit-btn");
    const cancelBtn = document.getElementById("cancel-btn");

    createDocumentBtn.addEventListener("click", function() {
        createDocumentModal.classList.add("open");
        createDimmedBackground();
    });

    createDocumentSubmitBtn.addEventListener("click", function() {
        const documentName = documentNameInput.value.trim();
        if (documentName) {
            createNewDocument(documentName);
            createDocumentModal.classList.remove("open");
            removeDimmedBackground();
        }
    });

    cancelBtn.addEventListener("click", function() {
        createDocumentModal.classList.remove("open");
        removeDimmedBackground();
    });

    function createDimmedBackground() {
        const dimmedBackground = document.createElement("div");
        dimmedBackground.classList.add("dimmed-background");
        document.body.appendChild(dimmedBackground);
    }

    function removeDimmedBackground() {
        const dimmedBackground = document.querySelector(".dimmed-background");
        if (dimmedBackground) {
            document.body.removeChild(dimmedBackground);
        }
    }

    function createNewDocument(documentName) {
        const newDoc = {
            name: documentName,
            owner: "me",
            date: new Date().toLocaleDateString()
        };

        const listItem = document.createElement("li");
        listItem.innerHTML = `
            <div class="document-info">
                <span class="document-name">${newDoc.name}</span>
                <span class="document-owner"> ${newDoc.owner}</span>
                <span class="document-date">${newDoc.date}</span>
                <span class="options">...
                    <ul class="options-menu">
                        <li>Delete document</li>
                        <li>Add user</li>
                        <li>Delete user</li>
                    </ul>
                </span>
            </div>
        `;

        documentList.appendChild(listItem);
    }

   
  
    docs.forEach((doc) => {
      const listItem = document.createElement("li");
      listItem.innerHTML = `
        <div class="document-info">
          <span class="document-name">${doc.name}</span>
          <span class="document-owner"> ${doc.owner}</span>
          <span class="document-date">${doc.date}</span>
          <span class="options">...
            <ul class="options-menu">
              <li>Delete document</li>
              <li>Add user</li>
              <li>Delete user</li>
            </ul>
          </span>
        </div>
      `;
      documentList.appendChild(listItem);
    });
  }
);
