# agility-game
눈치게임

## Game rules
- Game goal: Do Not Lose.
- You must avoid sending the same bid number, "Next Bid", with other players (almost) at the same time.
- Multiple players sending the same Next Bid in the time interval of 600ms all become losers. (# of losers >= 2)
- "Last Bid" is the highest bid in a game until the moment.
- "Next Bid" is set as soon as any player sends bid with the same number as Last Bid. It then automatically increments after 600ms.

## 게임 규칙
- '거의' 동시에 같은 숫자를 비딩하는 플레이어들이 패배합니다. (시간 간격: 600ms)
- '비딩' 이란 "Next Bid"의 숫자를 콜하는 것 혹은 그 숫자를 말합니다.
- "Last Bid"는 게임 내에서 현재까지 가장 높은 비딩입니다.
- "Next Bid"는 아무 플레이어가 비딩하는 순간 Last Bid와 함께 같은 숫자가 됩니다. 그리곤 600ms 뒤에 자동으로 +1 만큼 증가합니다.

## Demo

### 1. Login

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/1-login.gif" width="850 height="500" /></div>

### 2. Joining games

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/2-join.gif" width="850 height="500" /></div>

### 3. Ready

Starting countdown begins when more than half of the players are ready.

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/3-ready.gif" width="850 height="500" /></div>

### 4. Cancel ready

You can cancel ready and reset starting countdown.

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/4-cancel-ready.gif" width="850 height="500" /></div>

### 5. Start game and bid

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/5-start-and-bid.gif" width="850 height="500" /></div>

### 6. Auto-bidding timer

If you don't send any bid in 10 seconds, browser automatically sends Next Bid. Try not to be lazy.

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/6-auto-bid.gif" width="500 height="300" /></div>

### 7. Ending

Two or more players sending the same Next Bid at the same time become losers.

<div><img src="https://github.com/rnd-cell-juhwan-jiho/agility-game/blob/master/demo/7-ending.gif" width="850 height="500" /></div>
