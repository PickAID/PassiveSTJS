PassiveSTJSEvents.ammoBurstAboutToEnd(event => {
  if (event.getReason() === 'ENERGY_DEPLETED') {
    event.enterZeroSustain(10, 20)
  }
})

PassiveSTJSEvents.ammoBurstSustain(event => {
  if (event.getRunIndex() >= 5) {
    event.terminate()
  } else {
    event.continueSustain()
  }
})
