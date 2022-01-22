const Chip = (props) => {

    const handleAnimationEnd = () => {
        let newChips = JSON.parse(JSON.stringify(props.chips))
        newChips[props.username] = false
        props.setChips(newChips)
    }

    return (
        <div className={"Chip" + (props.losers.includes(props.username)
            ? "Lost"
            : props.chips[props.username] ? "Animate" : "")}
            onAnimationEnd={handleAnimationEnd}>
                <span className="ChipUsername">{props.username}</span>
                {(props.losers.includes(props.username) || props.username === props.lastBidder)
                    && <span className="ChipLastBid">{props.lastBid}!</span>}
        </div>
    )
}

export default Chip
