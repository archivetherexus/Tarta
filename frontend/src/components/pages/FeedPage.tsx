import { Component } from 'inferno';
import { connect } from 'inferno-redux';

class FeedPage extends Component<any, any> {
    render() {
        return (
            <div className="panel">
                {'Not implemented yet.'}
            </div>
        )
    }
}

export default connect((state:any) => ({
}))(FeedPage);