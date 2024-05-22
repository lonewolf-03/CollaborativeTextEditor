import React, { useState } from "react";

const Documents = (props) => {

    const [ownedDocuments, setOwnedDocuments] = useState([]);
    const [sharedPermissions, setSharedPermissions] = useState([]);

    const getOwnedDocuments = async () => {
    
        try{
            let res = await fetch(props.uri + '/document/owned', {
                method : 'GET',
                headers : { 
                    'Content-Type' : 'application/json',
                    'Authorization' : props.jwt         
                 }        
            });
    
            if(!res.ok){
                let resBody = await res.json();
                let message = await resBody.message;
                throw Error(message);
            }
    
            
            let resBody = await res.json();
    
            setOwnedDocuments(resBody.body);
    
        } catch(error){
            alert(error.message);
        }
    }

    const getSharedPermissions = async() => {
        try{
            let res = await fetch(props.uri + '/document/owned', {
                method : 'GET',
                headers : { 
                    'Content-Type' : 'application/json',
                    'Authorization' : props.jwt         
                 }        
            });
    
            if(!res.ok){
                let resBody = await res.json();
                let message = await resBody.message;
                throw Error(message);
            }
    
            
            let resBody = await res.json();
    
            setSharedPermissions(resBody.body);
    
        } catch(error){
            alert(error.message);
        }
    }

    return(<></>);
}

export default Documents;