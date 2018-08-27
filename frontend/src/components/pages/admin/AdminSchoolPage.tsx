import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';

import { fetchHTTP } from '../../../helpers/fetch_http';


class AdminSchoolPage extends Component<any, {
    school: School | null,
}> {
    constructor() {
        super()

        this.state = {
            school: null,
        }
    }

    componentDidMount() {
        this.fetchSchool();
    }

    fetchSchool() {
        if (!this.state) {
            return;
        }
        console.dir(this.context.router.route.match.params);
        fetchHTTP<School>('http://localhost:3000/admin/school/get', {
            sessionID: this.props.session,
            slugName: this.context.router.route.match.params.slugName
        }).then(school => this.setState({school}));
    }

    onDeleteSchool() {
        if (!this.state || !this.state.school) {
            return;
        }
        fetchHTTP('http://localhost:3000/admin/school/delete', {
            sessionID: this.props.session,
            slugName: this.state.school.slugName,
        }).then(this.context.router.history.push('/admin'))
    }

    handleDeleteSchool = this.onDeleteSchool.bind(this);

    render() {
        if (!this.state) {
            return;
        }
        const { i18n } = this.props;
        const { school } = this.state;
        return school ? (
            <div className="sugar-panel">
                <u>{school.name}</u>
                <br />
                {'Slug Name: ' + school.slugName}
                <br />
                {'ID: ' + school.id}
                <br />
                <button className="sugar-button" onClick={this.handleDeleteSchool}>{i18n('Delete')}</button>
                <Link className="sugar-button" to="/admin">{i18n('Go back')}</Link>
            </div>
        ) : (
            <div>{i18n('Loading...')}</div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
    session: state.session,
}))(AdminSchoolPage);