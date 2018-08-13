export default class Post {
    title: string;
    content: string;
    constructor(title: string, content: string) {
        this.title = title;
        this.content = content;
    }
    static fromArray(array: [string, string]) {
        //console.log("Title: " + array[0] + ", Content: " + array[1]);
        return new Post(array[0], array[1]);
    }
}