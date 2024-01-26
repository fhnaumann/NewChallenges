import { useConfigStore } from '@/main';
import { json2xml, js2xml } from 'xml-js';
import schematronFile from "@/assets/constraints.sch?raw"
import { Schema } from 'node-schematron';



export function isValid(doChanges) {
    const store = useConfigStore().model
    console.log("early")
    console.log(store.$state)
    const copy = JSON.parse(JSON.stringify(store.$state))
    //console.log(jsonString)
    //console.log(store)
    doChanges(copy)
    console.log("rerunning validity check")
    const wrappedInRootCopy = {
        root: { ...copy }
    }
    console.log(wrappedInRootCopy)
    const options = {spaces: 2, compact: true}
    let xmlString = js2xml(wrappedInRootCopy, options)
    
    const schema = Schema.fromString(schematronFile)
    const result = schema.validateString(xmlString, { debug: true })
    console.log("result")
    console.log(result)
    const messages = result.map((violoation) => violoation.message.trim())
    if(messages) {
        console.log(messages)
    }
    console.log(messages.length == 0)
    return { 
        valid: messages.length == 0, 
        messages: messages }
    //return jsonObject == "End" && copy.rules.NoBlockBreak.punishments.Health?.affects == "All"
    // console.log("from validtor")
    
    // TODO:
    // Format to JSON String
    // Format to XML
    // Run Schematron
    // Get feedback
    // Display feedback
}