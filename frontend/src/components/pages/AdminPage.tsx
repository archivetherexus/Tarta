import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';
import { fetchHTTP } from '../../helpers/fetch_http';
import Tabs from '../tabs/Tabs';
import Tab from '../tabs/Tab';

class Admin extends Component<{
    i18n: (i: string) => string;
}, {
    schools: [School] | null, 
}> {
    
    constructor() {
        super();

        this.state = {
            schools: null
        };

        this.fetchSchools();
    }

    fetchSchools() {
        fetchHTTP<[School]>('http://localhost:3000/admin/school/list').then(list => this.setState({schools: list}));
    }

    schoolNameRef: HTMLInputElement | null = null;

    onNewSchoolSubmit() {
        if (this.schoolNameRef != null) {
            var schoolName = this.schoolNameRef.value;
            
            fetchHTTP('http://localhost:3000/admin/school/create', {
                name: schoolName,
            }).then(() => this.fetchSchools()).catch(e => alert(e));
        }
    }

    handleNewSchoolSubmit = this.onNewSchoolSubmit.bind(this);

    render() {
        if (this.state === null) {
            return;
        }
        const { i18n } = this.props;
        const { schools } = this.state;

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
                        {'Delete user.'}
                    </Tab>
                </Tabs>
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(Admin);