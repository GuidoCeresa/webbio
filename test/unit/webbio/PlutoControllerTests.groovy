package webbio

import grails.test.mixin.*

@TestFor(PlutoController)
@Mock(Pluto)
class PlutoControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/pluto/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.plutoInstanceList.size() == 0
        assert model.plutoInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.plutoInstance != null
    }

    void testSave() {
        controller.save()

        assert model.plutoInstance != null
        assert view == '/pluto/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/pluto/show/1'
        assert controller.flash.message != null
        assert Pluto.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/pluto/list'

        populateValidParams(params)
        def pluto = new Pluto(params)

        assert pluto.save() != null

        params.id = pluto.id

        def model = controller.show()

        assert model.plutoInstance == pluto
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/pluto/list'

        populateValidParams(params)
        def pluto = new Pluto(params)

        assert pluto.save() != null

        params.id = pluto.id

        def model = controller.edit()

        assert model.plutoInstance == pluto
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/pluto/list'

        response.reset()

        populateValidParams(params)
        def pluto = new Pluto(params)

        assert pluto.save() != null

        // test invalid parameters in update
        params.id = pluto.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/pluto/edit"
        assert model.plutoInstance != null

        pluto.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/pluto/show/$pluto.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        pluto.clearErrors()

        populateValidParams(params)
        params.id = pluto.id
        params.version = -1
        controller.update()

        assert view == "/pluto/edit"
        assert model.plutoInstance != null
        assert model.plutoInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/pluto/list'

        response.reset()

        populateValidParams(params)
        def pluto = new Pluto(params)

        assert pluto.save() != null
        assert Pluto.count() == 1

        params.id = pluto.id

        controller.delete()

        assert Pluto.count() == 0
        assert Pluto.get(pluto.id) == null
        assert response.redirectedUrl == '/pluto/list'
    }
}
