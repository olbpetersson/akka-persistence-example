import React, {Component} from 'react';
import {Jumbotron} from 'react-bootstrap';

import SicknessForm from './SicknessForm';

class Reception extends Component {
    render() {
        return (
            <Jumbotron>
                <h1>Hi</h1>
                <div>
                    <SicknessForm/>
                </div>
            </Jumbotron>
        );
    }
}

export default Reception;
