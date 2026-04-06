PassiveSTJSEvents.skillTreeContent(event => {
  const tree = event.createTree('kubejs:pi_test_tree')
  const root = tree.startingSkill('kubejs:pi_test_root')
    .title('PI Test Root')
    .position(0, 0)

  tree.skill('kubejs:pi_test_child')
    .title('PI Test Child')
    .position(48, 0)
    .connect(root)
})
