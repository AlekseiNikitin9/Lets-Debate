<script>
  export let gameCode = '';
</script>

<style>
  .top-bar {
    position: relative;
    top: 0;
    width: 100%;
    padding: 1rem;
    background: #333333;
    color: #ffffff;
    font-size: 1.2rem;
    font-weight: bold;
    text-align: center;
    z-index: 10;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
</style>

<div class="top-bar">
  {#if gameCode}
    Game Code: {gameCode}
  {:else}
    Debate Room
  {/if}
</div>
