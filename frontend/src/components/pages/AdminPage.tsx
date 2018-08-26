import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';
import { fetchHTTP } from '../../helpers/fetch_http';
import Tabs from '../tabs/Tabs';
import Tab from '../tabs/Tab';

class Admin extends Component<{
    i18n: (i: string) => string;
    session: string,
}, {
    schools: [School] | null, 
    users: [User] | null,
}> {
    
    constructor() {
        super();

        this.state = {
            schools: null,
            users: null,
        };
    }

    componentDidMount() {
        this.fetchSchools();
        this.fetchUsers();
    }

    fetchSchools() {
        fetchHTTP<[School]>('http://localhost:3000/admin/school/list').then(schools => this.setState({schools}));
    }

    fetchUsers() {
        fetchHTTP<[User]>('http://localhost:3000/admin/user/list').then(users => this.setState({users}))
    }

    schoolNameRef: HTMLInputElement | null = null;
    usernameRef: HTMLInputElement | null = null;
    passwordRef: HTMLInputElement | null = null;

    onNewSchoolSubmit() {
        if (this.schoolNameRef != null) {
            let schoolName = this.schoolNameRef.value;
            
            fetchHTTP('http://localhost:3000/admin/school/create', {
                name: schoolName,
            }).then(() => this.fetchSchools());
        }
    }

    onNewUserSubmit() {
        let { session } = this.props;
        if (this.usernameRef != null && this.passwordRef != null) {
            let username = this.usernameRef.value;
            let password = this.passwordRef.value;

            fetchHTTP('http://localhost:3000/admin/user/create', {
                username,
                password,
                sessionID: session
            }).then(() => this.fetchUsers());
        }
    }

    handleNewSchoolSubmit = this.onNewSchoolSubmit.bind(this);
    handleNewUserSubmit = this.onNewUserSubmit.bind(this);

    render() {
        if (this.state === null) {
            return;
        }
        const { i18n } = this.props;
        const { schools, users } = this.state;

        return (
            <div>
                <Tabs>
                    <Tab title="School">
                        <div>
                            {schools && schools.map(s => (
                                <div>
                                    <Link to={'/admin/school/' + s.name}>{s.name}</Link>
                                    <br />
                                </div>
                            ))}
                        </div>
                        <br />
                        <input className="sugar-input" ref={(r) => this.schoolNameRef = r} />
                        <button className="sugar-button" onClick={this.handleNewSchoolSubmit}>{i18n('Create')}</button>
                    </Tab>
                    <Tab title="Test">
                        {'Test 2'}
                    </Tab>
                    <Tab title="Users">
                        <div>
                            {users && users.map(user => (
                                <div>
                                    <Link to={'/admin/user/' + user.username}>{user.username}</Link>
                                    <br />
                                </div>
                            ))}
                        </div>
                        <b>{i18n('Create a new user')}</b>
                        <br />
                        {i18n('Username')}
                        <input className="sugar-input" ref={(r) => this.usernameRef = r} />
                        <br />
                        {i18n('Password')}
                        <input className="sugar-input" ref={(r) => this.passwordRef = r} />
                        <button className="sugar-button" onClick={this.handleNewUserSubmit}>{i18n('Create')}</button>
                    </Tab>
                </Tabs>
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
    session: state.session,
}))(Admin);