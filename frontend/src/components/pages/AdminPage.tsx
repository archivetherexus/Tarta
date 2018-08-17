import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { fetchHTTP } from '../../helpers/fetch_http';

import School from '../../models/School';

class Admin extends Component<any, {
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
        fetchHTTP('http://localhost:3000/admin/school/list').then(list => list !== null && this.setState({schools: list.map(School.fromArray)}));
    }

    schoolNameRef: HTMLInputElement | null = null;

    onNewSchoolSubmit() {
        if (this.schoolNameRef != null) {
            var schoolName = this.schoolNameRef.value;
            
            fetchHTTP('http://localhost:3000/admin/school/create', {
                name: schoolName,
            }).then(() => this.context.router.history.push('/feed'));
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
            <div className="panel popup">
                <b>{i18n('School')}</b>
                <div>
                    {schools && schools.map(s => (
                        <div>
                            {s.name}
                            <br />
                        </div>
                    ))}
                </div>
                <br />
                <input ref={(r) => this.schoolNameRef = r} />
                <button onClick={this.handleNewSchoolSubmit}>{i18n('Create')}</button>
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(Admin);