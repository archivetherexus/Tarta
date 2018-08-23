import { Component, linkEvent, VNode } from 'inferno';

export default class Tabs extends Component<{
    children: JSX.Element[],
}, {
    tab: number,
}> {
    constructor() {
        super()

        this.state = {
            tab: 0
        };
    }

    selectTab(data: {tab: Tabs, i: number}) {
        data.tab.setState({
            tab: data.i,
        });
    }

    handleSelectTab = this.selectTab.bind(this);

    render() {
        if (this.state == null) {
            return;
        }

        if (this.props.children == null) {
            return;
        }

        const { children } = this.props;
        const { tab } = this.state;

        return (
            <div className="tabs-component">
                <div className="tabs-component-upper">
                    {children.map((t, i) => {
                        return tab == i ? (
                            <span onClick={linkEvent({tab: this, i}, this.selectTab)} className="active tab-component">{t.props.title}</span>
                        ) : (
                            <span onClick={linkEvent({tab: this, i}, this.selectTab)} className="tab-component">{t.props.title}</span>
                        );
                    })}
                </div>
                <div className="tabs-component-lower">
                    {children[tab].props.children}
                </div>
            </div>
        );
    }
}
