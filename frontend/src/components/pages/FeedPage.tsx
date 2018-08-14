import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';
import { fetchHTTP } from '../../helpers/fetch_http';
import Post from '../../models/Post';

class FeedPage extends Component<any, {
    feed: Post[] | null
}> {
    constructor() {
        super();
        this.fetchFeed();
        this.state = {
            feed: null,
        };
    }
    parseFeed(feed: [string, string][]): Post[] {
        console.log("got so far!");
        console.log(feed);
        var arr = feed.map(Post.fromArray);
        console.log("Mapped:");
        console.log(arr);
        return arr;
    }
    fetchFeed() {
        fetchHTTP('http://localhost:3000/posts/feed/get')
        .then(feed => feed !== null && this.setState({feed: this.parseFeed(feed)}));
    }
    render() {
        if (this.state === null) {
            return null;
        }

        const { feed } = this.state;
        console.log(feed);

        return (
            <div className="feed-page panel">
                <div>
                    {feed !== null ? (
                        feed.map(post => (
                            <div>
                                <h3>{post.title}</h3>
                                {post.content}
                                <hr />
                            </div>
                        ))
                    ) : (
                        'Loading ...'
                    )}
                </div>
                <Link to="/create">{'Create a new post.'}</Link>
            </div>
        )
    }
}

export default connect((state:any) => ({
}))(FeedPage);