import grails.test.mixin.*

@TestFor(NazioneController)
@Mock(Nazione)
class NazioneControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }


    void testIndex() {
        controller.index()
        assert "/nazione/list" == response.redirectedUrl
    }


    void testList() {

        def model = controller.list()

        assert model.nazioneInstanceList.size() == 0
        assert model.nazioneInstanceTotal == 0
    }


    void testCreate() {
        def model = controller.create()

        assert model.nazioneInstance != null
    }


    void testSave() {
        controller.save()

        assert model.nazioneInstance != null
        assert view == '/nazione/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/nazione/show/1'
        assert controller.flash.message != null
        assert Nazione.count() == 1
    }


    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/nazione/list'

        populateValidParams(params)
        def nazione = new Nazione(params)

        assert nazione.save() != null

        params.id = nazione.id

        def model = controller.show()

        assert model.nazioneInstance == nazione
    }


    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/nazione/list'

        populateValidParams(params)
        def nazione = new Nazione(params)

        assert nazione.save() != null

        params.id = nazione.id

        def model = controller.edit()

        assert model.nazioneInstance == nazione
    }


    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/nazione/list'

        response.reset()

        populateValidParams(params)
        def nazione = new Nazione(params)

        assert nazione.save() != null

        // test invalid parameters in update
        params.id = nazione.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/nazione/edit"
        assert model.nazioneInstance != null

        nazione.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/nazione/show/$nazione.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        nazione.clearErrors()

        populateValidParams(params)
        params.id = nazione.id
        params.version = -1
        controller.update()

        assert view == "/nazione/edit"
        assert model.nazioneInstance != null
        assert model.nazioneInstance.errors.getFieldError('version')
        assert flash.message != null
    }


    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/nazione/list'

        response.reset()

        populateValidParams(params)
        def nazione = new Nazione(params)

        assert nazione.save() != null
        assert Nazione.count() == 1

        params.id = nazione.id

        controller.delete()

        assert Nazione.count() == 0
        assert Nazione.get(nazione.id) == null
        assert response.redirectedUrl == '/nazione/list'
    }
}
