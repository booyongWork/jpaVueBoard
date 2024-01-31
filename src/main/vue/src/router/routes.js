import BoardListView from '../views/BoardListView.vue';
import BoardReadView from '../views/BoardReadView.vue';
import BoardRegisterView from '../views/BoardRegisterView.vue';
import BoardModifyView from "../views/BoardModifyView.vue";

const routes = [
  {
    path: '/boardList',
    name: 'BoardListView',
    components: {
      default: BoardListView
    }
  },
  {
    path: '/:no',
    name: 'BoardReadView',
    components: {
      default: BoardReadView
    },
    props: {
      default: true
    }
  },
  {
    path: '/register',
    name: 'BoardRegisterView',
    components: {
      default: BoardRegisterView
    }
  },
  {
    path: '/:no/modify',
    name: 'BoardModifyView',
    components: {
      default: BoardModifyView
    },
    props: {
      default: true
    }
  },
]

export default routes
