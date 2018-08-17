export default class School {
    name: string;
    constructor(name: string) {
        this.name = name;
    }
    static fromArray(array: [string]) {
        return new School(array[0]);
    }
}