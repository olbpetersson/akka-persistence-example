import React, {Component} from 'react';
import {FormGroup, FormControl, ControlLabel, Button} from 'react-bootstrap';
var component;

class SicknessForm extends Component {

    constructor(props){
        super(props);
        console.log("cdm");

        component = this;
        this.handleChange = this.handleChange.bind(this);
        this.setupSocket = this.setupSocket.bind(this);
        this.setState = this.setState.bind(this);
        this.submit = this.submit.bind(this);
        this.snapshot = this.snapshot.bind(this);
        this.fail = this.fail.bind(this);
        this.readIllnesses = this.readIllnesses.bind(this);

        this.state = { value: '', loading: true};

        this.ws = new WebSocket("ws://localhost:9000/ws/");
        this.setupSocket(this.ws);

    }

    handleChange(e) {
        this.setState({ value: e.target.value});
    }

    submit(){
        console.log("Submitting");
        console.log(this.state.value);
        var reportIllness = {type: "ReportIllnessCmd", cmd: {illness: {symptome: this.state.value}}};
        console.log(reportIllness);
        this.ws.send(JSON.stringify(reportIllness));
        this.setState({value: ''});
    }

    snapshot(){
        console.log("snapshotting");
        console.log(this.state.value);
        var snapshot = {type: "SnapshotCmd", cmd: {}};
        console.log(snapshot);
        this.ws.send(JSON.stringify(snapshot));
    }

    fail(){
        console.log("failing");
        var failure = {type: "FailureCMD"};
        console.log(failure);
        this.ws.send(JSON.stringify(failure));
    }

    readIllnesses(){
        var readCmd = {type: "ReadIllnessesCmd"};
        console.log(readCmd);
        this.ws.send(JSON.stringify(readCmd));
    }

    render() {
        let loading = this.state.loading;
        return (
            <form>
                <FormGroup
                    controlId="formBasicText">
                    <ControlLabel>Report an illness</ControlLabel>
                    <FormControl
                        type="text"
                        value={this.state.value}
                        placeholder="Illness"
                        onChange={this.handleChange}/>
                    <FormControl.Feedback />
                </FormGroup>
                <Button
                    disabled={loading}
                    onClick={this.submit}>
                        Submit
                </Button>
                <Button
                    disabled={loading}
                    onClick={this.snapshot}>
                    Snapshot
                </Button>
                <Button
                    disabled={loading}
                    onClick={this.fail}>
                    Crasch it!
                </Button>
                <Button
                    disabled={loading}
                    onClick={this.readIllnesses}>
                    Read Illnesses!
                </Button>
            </form>
        );
    }


    setupSocket = function(webSocket){
        webSocket.onopen = function(){
            component.setState({loading: false});
            console.log("socket open and ready!")
        };

        webSocket.onclose = function(){
            component.setState({loading: true});
        }
        webSocket.onmessage = function(msg){
            console.log(msg.data);
        }
    }
}



export default SicknessForm;
