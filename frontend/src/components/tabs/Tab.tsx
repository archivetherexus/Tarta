import { Component, InfernoChildren } from 'inferno';

export default class Tab extends Component<{
    title: string,
    children: InfernoChildren,
}, any> {
    title: string;

    children: InfernoChildren;

    constructor() {
        super()

        this.title = this.props.title;
        this.children = this.props.children;
    }

    render() {
        return this.children;
    }
}
