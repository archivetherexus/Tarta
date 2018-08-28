import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';

import { fetchHTTP } from '../../../helpers/fetch_http';


class AdminSchoolPage extends Component<any, {
    school: School | null,
    groups: [Group] | null,
}> {
    constructor() {
        super()

        this.state = {
            school: null,
            groups: null,
        }
    }

    componentDidMount() {
        this.fetchSchool();
        this.fetchGroup();
    }

    fetchSchool() {
        if (!this.state) {
            return;
        }
        fetchHTTP<School>('http://localhost:3000/admin/school/get', {
            sessionID: this.props.session,
            slugName: this.context.router.route.match.params.slugName
        }).then(school => this.setState({school}));
    }

    fetchGroup() {
        if (!this.state) {
            return;
        }
        fetchHTTP<[Group]>('http://localhost:3000/admin/school/group/list', {
            sessionID: this.props.session,
            slugName: this.context.router.route.match.params.slugName
        }).then(groups => this.setState({groups}));
    }

    groupNameRef: HTMLInputElement | null = null;

    onDeleteSchool() {
        if (!this.state || !this.state.school) {
            return;
        }
        fetchHTTP('http://localhost:3000/admin/school/delete', {
            sessionID: this.props.session,
            slugName: this.state.school.slugName,
        }).then(() => this.context.router.history.push('/admin'))
    }

    onCreateGroup() {
        if (!this.state || !this.state.school || !this.groupNameRef) {
            return;
        }
        fetchHTTP('http://localhost:3000/admin/school/group/create', {
            sessionID: this.props.session,
            groupName: this.groupNameRef.value,
            slugName: this.state.school.slugName,
        }).then(() => this.fetchGroup());
    }

    handleDeleteSchool = this.onDeleteSchool.bind(this);
    handleCreateGroup = this.onCreateGroup.bind(this);

    render() {
        if (!this.state) {
            return;
        }
        const { i18n } = this.props;
        const { school, groups} = this.state;
        return school ? (
            <div className="sugar-panel">
                <u>{school.name}</u>
                <br />
                {'Slug Name: ' + school.slugName}
                <br />
                {'ID: ' + school.id}
                <br />
                <div className="sugar-border">
                    <b>{i18n('Groups')}</b>
                    <div>
                        {groups ? (
                            groups.map(group => (
                                <div>
                                    {group.name}
                                    <br />
                                </div>
                            ))
                        ) : (
                            'Loading...'
                        )}
                    </div>
                    <input className="sugar-input" ref={e => this.groupNameRef = e} />
                    <button className="sugar-button" onClick={this.handleCreateGroup}>{i18n('Add Group')}</button>
                </div>
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