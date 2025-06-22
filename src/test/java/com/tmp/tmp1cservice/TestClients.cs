using System.Net;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using Moq;
using Newtonsoft.Json;
using Tmp1CService.DTOs.ClientDTOs;
using Tmp1CService.Models;
using Tmp1CService.Repositories.Interfaces;

namespace Tmp1CService.Tests;

public class TestClientsController : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;
    private readonly AppDbContext _db;
    private readonly CustomWebApplicationFactory<Program> _factory;

    public TestClientsController(CustomWebApplicationFactory<Program> factory)
    {
        _factory = factory;
        _client = factory.CreateClient();
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase("TestDb")
            .Options;
        _db = new AppDbContext(options);
    }

    private HttpClient CreateClientWithMock(Action<Mock<IClientRepository>> mockSetup = null!)
    {
        var mockRepo = new Mock<IClientRepository>();
        mockSetup.Invoke(mockRepo);

        return _factory.WithWebHostBuilder(builder =>
        {
            builder.ConfigureServices(services =>
            {
                services.RemoveAll<IClientRepository>();
                services.AddScoped(_ => mockRepo.Object);
            });
        }).CreateClient();
    }

    private static void AddApiKey(HttpClient client)
    {
        client.DefaultRequestHeaders.Add("X-API-KEY", "qweasd");
    }

    private static Client GetTestClient(Guid? id = null)
    {
        return new Client
        {
            Id = id ?? Guid.NewGuid(),
            Login = "TestClient",
            Password = "123",
            Url1C = "http://localhost/1CTest"
        };
    }

    private static ClientDto GetTestClientDto()
    {
        return new ClientDto
        {
            Login = "TestClient",
            Password = "123",
            Url1C = "http://localhost/1CTest"
        };
    }

    #region GetClients

    #region return200

    [Fact]
    public async Task GetClients_ReturnsOk_WhenClientsExist()
    {
        // Arrange
        var clients = new List<Client> { GetTestClient(), GetTestClient() };
        var client = CreateClientWithMock(mock => mock.Setup(repo => repo.GetClients()).ReturnsAsync(clients));
        AddApiKey(client);

        // Act
        var response = await client.GetAsync("api/clients");

        // Assert
        var responseContent = await response.Content.ReadAsStringAsync();
        var returnedClients = JsonConvert.DeserializeObject<List<Client>>(responseContent);

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.NotNull(returnedClients);
        Assert.Equal(2, returnedClients.Count);
    }

    #endregion

    #region return401

    [Fact]
    public async Task GetClients_ReturnsUnauthorized_WhenNoApiKey()
    {
        // Act
        var response = await _client.GetAsync("api/clients");

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #endregion

    #region GetClient

    #region return200

    [Fact]
    public async Task GetClient_Returns200_WhenClientExists()
    {
        // Arrange
        var clientFromDb = GetTestClient();
        var client = CreateClientWithMock(mock =>
            mock.Setup(repo => repo.GetClient(clientFromDb.Id)).ReturnsAsync(clientFromDb));
        AddApiKey(client);

        // Act
        var response = await client.GetAsync($"api/clients/{clientFromDb.Id}");

        // Assert
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        var content = await response.Content.ReadAsStringAsync();
        var returnedClient = JsonConvert.DeserializeObject<Client>(content);
        Assert.NotNull(returnedClient);
        Assert.Equal(clientFromDb.Id, returnedClient.Id);
    }

    #endregion

    #region return400

    [Fact]
    public async Task GetClient_Returns400_WhenInvalidGuid()
    {
        // Arrange
        AddApiKey(_client);

        // Act
        var result = await _client.GetAsync("api/clients/InvalidId");

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task GetClient_Returns401_WhenNoApiKey()
    {
        // Act
        var result = await _client.GetAsync($"api/clients/{Guid.Empty}");

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, result.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task GetClient_Returns404_WhenClientDoesNotExist()
    {
        // Arrange
        var clientId = Guid.NewGuid();
        AddApiKey(_client);

        // Act
        var result = await _client.GetAsync($"api/clients/{clientId}");

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, result.StatusCode);
    }

    #endregion

    #endregion

    #region RegisterClient

    #region return201

    [Fact]
    public async Task RegisterClient_ReturnsCreatedAndAddsUserToDb()
    {
        // Arrange
        var clientDto = GetTestClientDto();
        var clientId = Guid.NewGuid();
        var client = CreateClientWithMock(mock =>
            mock.Setup(repo => repo.RegisterClient(It.IsAny<ClientDto>())).ReturnsAsync(clientId)
        );
        AddApiKey(client);
        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");

        // Act
        var response = await client.PostAsync("api/clients", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
        var responseContent = await response.Content.ReadAsStringAsync();
        var returnedValue = JsonConvert.DeserializeObject<Dictionary<string, Guid>>(responseContent);
        var createdClientId = returnedValue!["id1c"];

        Assert.Equal(clientId, createdClientId);
    }

    #endregion

    #region return400

    [Fact]
    public async Task RegisterClient_ReturnsBadRequest_WhenClientDtoIsInvalid()
    {
        // Arrange
        var invalidClient = new ClientDto();
        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(invalidClient), Encoding.UTF8, "application/json");
        AddApiKey(_client);

        // Act
        var result = await _client.PostAsync("api/clients", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task RegisterClient_Returns401_WhenNoApiKey()
    {
        // Arrange
        var invalidClient = new ClientDto();
        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(invalidClient), Encoding.UTF8, "application/json");

        // Act
        var result = await _client.PostAsync("api/clients", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, result.StatusCode);
    }

    #endregion

    #endregion

    #region UpdateClient

    #region return200

    [Fact]
    public async Task UpdateClient_Returns200_WhenClientExists()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var clientDto = GetTestClientDto();
        var client = CreateClientWithMock(mock =>
        {
            mock.Setup(repo => repo.GetClient(client1C.Id)).ReturnsAsync(client1C);
            mock.Setup(repo => repo.UpdateClient(It.IsAny<ClientDto>(), It.IsAny<Guid>())).ReturnsAsync(1);
        });
        AddApiKey(client);

        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");

        // Act
        var response = await client.PutAsync($"api/clients/{client1C.Id}", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    #endregion

    #region return400

    [Fact]
    public async Task UpdateClient_Returns400_WhenInvalidGuid()
    {
        // Arrange
        var clientDto = GetTestClientDto();
        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");
        AddApiKey(_client);

        // Act
        var response = await _client.PutAsync("api/clients/invalid-guid", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    [Fact]
    public async Task UpdateClient_Returns400_WhenClientDtoIsNull()
    {
        // Arrange
        AddApiKey(_client);

        // Act
        var response = await _client.PutAsync($"api/clients/{Guid.NewGuid()}", null);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    [Fact]
    public async Task UpdateClient_Returns400_WhenGuidEmpty()
    {
        // Arrange
        AddApiKey(_client);

        // Act
        var response = await _client.PutAsync($"api/clients/{Guid.Empty}", null);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task UpdateClient_Returns401_WhenUnauthorized()
    {
        // Act
        var response = await _client.PutAsync($"api/clients/{Guid.NewGuid()}", null);

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task UpdateClient_Returns404_WhenClientNotFound()
    {
        // Arrange
        var clientId = Guid.NewGuid();
        var clientDto = GetTestClientDto();
        var client =
            CreateClientWithMock(mock => mock.Setup(repo => repo.GetClient(clientId)).ReturnsAsync((Client?)null));
        AddApiKey(client);

        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");

        // Act
        var response = await client.PutAsync($"api/clients/{clientId}", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    #endregion

    #endregion

    #region DeleteClient

    #region return204

    [Fact]
    public async Task DeleteClient_Returns204_WhenClientExists()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();
        AddApiKey(_client);

        // Act
        var response = await _client.DeleteAsync($"api/clients/{client1C.Id}");

        // Assert
        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
        var clientInDb = await _db.Clients.FirstOrDefaultAsync(c => c.Id == client1C.Id);
        Assert.Null(clientInDb);
    }

    #endregion

    #region return401

    [Fact]
    public async Task DeleteClient_Returns401_WhenUnauthorized()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        // Act
        var response = await _client.DeleteAsync($"api/clients/{client1C.Id}");

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task DeleteClient_Returns404_WhenClientNotFound()
    {
        // Arrange
        var clientId = Guid.NewGuid();
        AddApiKey(_client);

        // Act
        var response = await _client.DeleteAsync($"api/clients/{clientId}");

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    #endregion

    #endregion

    #region TestConnections

    #region return200

    [Fact]
    public async Task TestConnections_ReturnsOk_WhenConnectionIsSuccessful()
    {
        // Arrange
        var clientDto = GetTestClientDto();
        var client = CreateClientWithMock(mock =>
            mock.Setup(repo => repo.TestConnection(It.IsAny<ClientDto>())).ReturnsAsync(true));
        AddApiKey(client);

        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");

        // Act
        var result = await client.PostAsync("api/clients/test", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.OK, result.StatusCode);
    }

    #endregion

    #region return400

    [Fact]
    public async Task TestConnection_ReturnsBadRequest_WhenUrlIsInvalid()
    {
        // Arrange
        var clientDto = new ClientDto { Login = "Web1", Password = "123", Url1C = "invalid-url" };
        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");
        AddApiKey(_client);

        // Act
        var result = await _client.PostAsync("api/clients/test", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    [Fact]
    public async Task TestConnections_ReturnsBadRequest_WhenConnectionFails()
    {
        // Arrange
        var clientDto = GetTestClientDto();
        var client = CreateClientWithMock(mock =>
            mock.Setup(repo => repo.TestConnection(It.IsAny<ClientDto>())).ReturnsAsync(false));
        AddApiKey(client);

        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");

        // Act
        var result = await client.PostAsync("api/clients/test", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, result.StatusCode);
    }

    #endregion

    #region return401

    [Fact]
    public async Task TestConnection_ReturnUnauthorized_WhenNoApiKey()
    {
        // Arrange
        var clientDto = new ClientDto { Login = "Web1", Password = "123", Url1C = "invalid-url" };
        using var clientJsonContent =
            new StringContent(JsonConvert.SerializeObject(clientDto), Encoding.UTF8, "application/json");

        // Act
        var result = await _client.PostAsync("api/clients/test", clientJsonContent);

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, result.StatusCode);
    }

    #endregion

    #endregion
}