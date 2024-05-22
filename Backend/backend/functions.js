

async function register(uri, firstName, lastName, email, password, confirmedPassword){
    const data = {
        firstName : firstName,
        lastName : lastName,
        email : email,
        password : password,
        confirmedPassword : confirmedPassword
    }

    return fetch(uri + '/register', {
        method : 'POST',
        headers : { 'Content-Type' : 'application/json' },
        body : JSON.stringify(data)
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );

}

async function confirmRegistration(uri, email, token) {
    const data = {
        email : email,
        token : token
    };


    return fetch(uri + '/register/confirm', {
        method : 'POST',
        headers : { 'Content-Type' : 'application/json' },
        body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function login(uri, email, password) {
    const data = {
        email : email,
        password : password
    };

    return fetch(uri + '/login', {
        method : 'POST',
        headers : { 'Content-Type' : 'application/json' },
        body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function getDocument(uri, jwtoken, id) {
    return fetch(uri+'/document/'+id, {
        method : 'GET',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken         
         }        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function updateDocument(uri, jwtoken, id, newContent, name) {
    const data = {
        newContent : newContent,
        name : name
    };

    return fetch(uri+'/document/update/'+id, {
        method : 'PUT',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         },
         body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                console.log(response.statusText);
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function getOwnedDocuments(uri, jwtoken) {
    return fetch(uri+'/document/owned', {
        method : 'GET',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         }        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function addDocument(uri, jwtoken, content, name){
    const data = {
        content: content,
        name : name
    };

    return fetch(uri+'/document/add', {
        method : 'POST',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         },
         body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function deleteDocument(uri, jwtoken, id){
    return fetch(uri+'/document/delete/'+id, {
        method : 'DELETE',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         }     
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function getDocumentPermissions(uri, jwtoken, id){
    return fetch(uri+'/document/shared/'+id, {
        method : 'GET',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         }     
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function getSharedPermissions(uri, jwtoken){
    return fetch(uri+'/document/shared/user', {
        method : 'GET',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         }     
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function addToControlAccessList(uri, jwtoken, email, docId, docName, permissions){
    const data = {
        email : email,
        docId : docId,
        docName : docName,
        permissions : permissions
    };

    return fetch(uri+'/document/shared/add', {
        method : 'POST',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         },
         body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function removeFromControlAccessList(uri, jwtoken, email, docId){
    const data = {
        email : email,
        docId : docId
    };

    return fetch(uri+'/document/shared/remove', {
        method : 'DELETE',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         },
         body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function updatePermission(uri, jwtoken, docId, email, newPermission) {
    const data = {
        docId : docId,
        email : email,
        newPermission : newPermission
    };

    return fetch(uri+'/document/shared/update', {
        method : 'PUT',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         },
         body : JSON.stringify(data)        
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.text();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}

async function deleteUser(uri, jwtoken, email){
    return fetch(uri+'/user/delete/'+email, {
        method : 'DELETE',
        headers : { 
            'Content-Type' : 'application/json',
            'Authorization' : jwtoken,         
         }     
    }).then(
        response => {
            if(!response.ok){
                return response.statusText;
            }

            return response.json();
        }
    ).catch(
        error => {
            console.log(error);
            throw error;
        }
    );
}


res = await login(uri, 'mohamed.kornay03@eng-st.cu.edu.eg', 'abcd1234')
res1 = await login(uri, 'moyman200311@gmail.com', 'qwerty4321')
res2 = await login(uri, 'mendelmoisha@gmail.com', 'r920n6dhqaz123')
res3 = await login(uri, 'emanayman1@yahoo.com', '123456789')

document.getSelection().addRange()