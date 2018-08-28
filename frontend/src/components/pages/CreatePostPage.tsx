import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { fetchHTTP } from '../../helpers/fetch_http';

class CreatePostPage extends Component<any, {
    groups: [Group] | null,
}> {
    
    constructor() {
        super();

        this.state = {
            groups: null,
        }
    }

    componentDidMount() {
        this.fetchGroups();
    }

    titleRef: HTMLInputElement | null = null;
    contentRef: HTMLInputElement | null = null;
    selectRef: HTMLSelectElement | null = null;

    onPostSubmit() {
        if (this.titleRef != null && this.contentRef != null && this.selectRef != null) {
            var title = this.titleRef.value;
            var content = this.contentRef.value;
            var recipient = this.selectRef.value;
            
            fetchHTTP('http://localhost:3000/posts/create', {
                title,
                content,
                recipient,
                sessionID: this.props.session,
            }, {post: true, form: true}).then(() => this.context.router.history.push('/feed'));
        }
    }

    fetchGroups() {
        fetchHTTP<[Group]>('http://localhost:3000/group/list/get', {
            sessionID: this.props.session,
        }).then(groups => this.setState({groups}));
    }

    handlePostSubmit = this.onPostSubmit.bind(this);

    render() {
        if (!this.state) {
            return;
        }

        const { i18n } = this.props;
        const { groups } = this.state;

        return (
            <div className="panel popup">
                <b>{i18n('Title')}</b>
                <br />
                <input ref={r => this.titleRef = r} />
                <br />
                <b>{i18n('Content')}</b>
                <br />
                <input ref={r => this.contentRef = r} />
                <br />
                <select ref={r => this.selectRef = r}>
                    {groups && groups.map(group => (
                        <option value={group.slugName}>{group.name}</option>
                    ))}
                </select>
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