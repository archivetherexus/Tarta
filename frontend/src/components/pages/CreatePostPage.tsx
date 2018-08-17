import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { fetchHTTP } from '../../helpers/fetch_http';

class CreatePostPage extends Component<any, any> {
    
    titleRef: HTMLInputElement | null = null;
    contentRef: HTMLInputElement | null = null;

    onPostSubmit() {
        if (this.titleRef != null && this.contentRef != null) {
            var title = this.titleRef.value;
            var content = this.contentRef.value;
            
            fetchHTTP('http://localhost:3000/posts/create', {
                title: title,
                content: content,
                sessionID: this.props.session,
            }, {post: true, form: true}).then(() => this.context.router.history.push('/feed'));
        }
    }

    handlePostSubmit = this.onPostSubmit.bind(this);

    render() {
        const { i18n } = this.props;

        return (
            <div className="panel popup">
                <b>{i18n('Title')}</b>
                <br />
                <input ref={(r) => this.titleRef = r} />
                <br />
                <b>{i18n('Content')}</b>
                <br />
                <input ref={(r) => this.contentRef = r} />
                <br />
                <button onClick={this.handlePostSubmit}>{i18n('Submit')}</button>
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
    session: state.session,
}))(CreatePostPage);