const Chip = (props) => {

    const handleAnimationEnd = () => {
        let newChips = JSON.parse(JSON.stringify(props.chips))
        newChips[props.username] = false
        props.setChips(newChips)
    }

    return (
        <div className={"Chip" + (props.chips[props.username] ? "Animate" : "")} 
            onAnimationEnd={handleAnimationEnd}>
            {props.username}
        </div>
    )
}

export default Chip
