import React, {Component} from 'react';
import {Grid} from 'react-bootstrap';

import Reception from './Reception'

class Container extends Component {
    render() {
        return (
            <Grid>
                <Reception />
            </Grid>
        );
    }
}

export default Container;
