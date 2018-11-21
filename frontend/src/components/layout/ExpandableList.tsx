import { VNode, Component } from 'inferno';

type ExpandableListProps = {
    data: {title: string, component: any}[],
}

type ExpandableListState = {
    openState: boolean[],
}

export default class ExpandableList extends Component<ExpandableListProps, ExpandableListState> {
    constructor() {
        super();
        console.log(this.props);
        this.state = {
            openState: [],
        };
    }

    componentWillMount() {
        this.setState({
            openState: this.props.data.map(() => false)
        });
    }

    componentWillReceiveProps(props: ExpandableListProps) {
        console.log('Recieve props!');
        this.setState({
            openState: props.data.map(() => false)
        });
    }

    bindToggleState = (i: number) => {
        return () => {
            this.setState((prevState: ExpandableListState) => {
                const openState = prevState.openState.slice();
                openState[i] = !openState[i];
                return {openState};
            });
        }
    }

    render() {
        if (this.state === null) {
            return;
        }

        return <div className="expandable-list">
            {this.state.openState.map((e, i) => (
                <div className="expandable-list__node">
                    <button onClick={this.bindToggleState(i)}>{this.props.data[i].title}</button>
                    {e ? this.props.data[i].component : null}
                </div>
            ))}
        </div>;

        
    }
}